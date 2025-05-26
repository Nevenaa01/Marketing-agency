import { Injectable } from '@angular/core';
import {
  CanActivate,
  UrlTree,
  Router,
  ActivatedRouteSnapshot,
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { User } from './model/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  async canActivate(route: ActivatedRouteSnapshot): Promise<boolean | UrlTree> {
    const user: User = this.authService.user$.getValue();
    
    const logged = await this.authService.isLogged();
    
    if (!logged) {
        this.router.navigate(['login']);
        return false;
    }

    if(route.url[0].path == 'user-profile'){
      if(user.role != 'ROLE_USER'){
        this.router.navigate(['home']);
        return false;
      }
      if(route.url[1].path != user.id.toString()){
        this.router.navigate(['user-profile/'+user.id.toString()]);
        return false;
      }
    }

    if(route.url[0].path == 'create-request'){
      if(user.role != 'ROLE_USER'){
        this.router.navigate(['home']);
        return false;
      }
    }

    if(route.url[0].path == 'your-advertisement'){
      if(user.role != 'ROLE_USER'){
        this.router.navigate(['home']);
        return false;
      }
    }

    if(route.url[0].path == 'employee-profile'){
      if(user.role != 'ROLE_MODERATOR' || route.url[1].path != user.id.toString()){
        this.router.navigate(['home']);
        return false;
      }
    }
    else if(route.url[0].path == 'requests'){
      if(user.role != 'ROLE_MODERATOR'){
        this.router.navigate(['home']);
        return false;
      }
    }

    return true;
  } 
}
