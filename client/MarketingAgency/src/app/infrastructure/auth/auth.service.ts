import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { TokenStorage } from './jwt/token.service';
import { environment } from 'src/env/environment';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Login } from './model/login.model';
import { AuthenticationResponse } from './model/authentication-response.model';
import { User } from './model/user.model';
import { Registration } from './model/registration.model';
import { NewAccessTokenResponse } from './model/newAccessTokenResponse.model';
import { WebSocketService } from 'src/app/feature-modules/layout/web-socket.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  user$ = new BehaviorSubject<User>({ id: 0, role: "" });

  constructor(private http: HttpClient,
    private tokenStorage: TokenStorage,
    private router: Router,
    private webSocketService: WebSocketService) { }

  loginWithEmail(loginRequest: Login): Observable<AuthenticationResponse> {
    return this.http
      .post<AuthenticationResponse>(environment.apiHost + 'auth/passwordless', loginRequest);
  }  
  confirmLoginWithEmail(token: string): Observable<AuthenticationResponse> {
    return this.http
      .get<AuthenticationResponse>(environment.apiHost + 'auth/confirm_login/'+token)
      .pipe(
        tap((authenticationResponse) => {
          this.tokenStorage.saveAccessToken(authenticationResponse.accessToken);
          this.tokenStorage.saveRefreshToken(authenticationResponse.refreshToken);
          this.setUser();
        })
      );
  }

    
  login(login: Login): Observable<AuthenticationResponse> {
    return this.http
      .post<AuthenticationResponse>(environment.apiHost + 'auth/signin', login)
      .pipe(
        tap((authenticationResponse) => {
          this.tokenStorage.saveAccessToken(authenticationResponse.accessToken);
          this.tokenStorage.saveRefreshToken(authenticationResponse.refreshToken);
          this.setUser();
        })
      );
  }

  register(registration: Registration): Observable<AuthenticationResponse> {
    return this.http
    .post<AuthenticationResponse>(environment.apiHost + 'auth/signup', registration)
  }

  logout(): void {
    this.router.navigate(['/home']).then(_ => {
      this.tokenStorage.clear();
      this.user$.next({id: 0, role: "" });
      }
    );
  }

  isLogged(): Promise<boolean>{
    const accessToken = this.tokenStorage.getAccessToken();
    if(!accessToken){
      return Promise.resolve(false);
    }

    return this.http.post<boolean>(environment.apiHost + 'auth/valid', accessToken)
    .toPromise()
    .then( result =>{
      this.user$.subscribe(user => {
        if(user.role=="ROLE_ADMIN"){
          this.webSocketService.initializeWebSocketConnection();
        }
      });
      return typeof result === 'boolean' ? result : false;
    })
    .catch( error => {
      if(error.status === 401){
        const refreshToken = this.tokenStorage.getRefreshToken();

        return this.http.post<NewAccessTokenResponse>(environment.apiHost + 'auth/refreshAccessToken', {accessToken, refreshToken})
          .toPromise()
          .then(newAccessToken =>{
            if(newAccessToken != null){
              this.tokenStorage.saveAccessToken(newAccessToken?.accessToken!)
              return true;
            }
            return false;
          })
          .catch(refreshError  => {
            return false;
          })
      }
      else{
        return false;
      }
    })
  }

  get2FAData(): Observable<any> {
    return this.http.get<any>(environment.apiHost + 'twoFactorAuth/2fa');
  }

  verify2FACode(secret: string, code: string, userEmail: string): Observable<any> {
    return this.http.post<any>('https://localhost:8443/api/twoFactorAuth/verify', { secret, code, userEmail});
  }

  checkIfUserExists(): void {
    const accessToken = this.tokenStorage.getAccessToken();
    if (accessToken == null) {
      return;
    }
    this.setUser();
  }

  private setUser(): void {
    const jwtHelperService = new JwtHelperService();
    const accessToken = this.tokenStorage.getAccessToken() || "";
    const user: User = {
      id: +jwtHelperService.decodeToken(accessToken).id,
      role: jwtHelperService.decodeToken(accessToken).role,
    };
    console.log(user)
    this.user$.next(user);
  }

  testDos(): Observable<string> {
    return this.http.get<string>(environment.apiHost + 'dos/author/resilience4j/10')
  }
}
