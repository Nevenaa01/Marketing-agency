import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { Login } from '../model/login.model';
import { WebSocketService } from 'src/app/feature-modules/layout/web-socket.service';

@Component({
  selector: 'xp-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(
    private authService: AuthService,
    private router: Router,
    private webSocketService: WebSocketService
  ) {}

  captchaResolved: boolean = false;
  token: string;
  loginFormVisible: boolean = true;
  secret: string = '';
  qrUrl: string = 'string';
  code: string = '';
  error: string = '';
  loginFields: Login;

  onCaptchaResolved(response: string): void {
    console.log(`Captcha resolved with response: ${response}`);
    this.token=response;
    this.captchaResolved = true;
  }

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });

  login(): void {
     this.loginFields = {
      email: this.loginForm.value.email || "",
      password: this.loginForm.value.password || "",
      token:this.token || "",
    };

    if (this.loginForm.valid) {
        if(this.token !== null && this.token !== undefined){
          this.loginFormVisible = false;

          this.authService.get2FAData().subscribe(data => {
            this.secret = data.secret;
            this.qrUrl = data.qrUrl;
          });
      }
      else{
        alert("You have to check all fields")
        // this.authService.login(this.loginFields).subscribe({
        //   next: () => {
        //     this.webSocketService.initializeWebSocketConnection();
        //     this.router.navigate(['/home']);
        //   },
        // });
      }
      
    }
  }

  verify(): void{
    if (this.code !== null) {
      this.authService.verify2FACode(this.secret, this.code, this.loginFields.email).subscribe(
        response => {
          if(response){
            this.authService.login(this.loginFields).subscribe({
              next: () => {
                this.webSocketService.initializeWebSocketConnection();
                this.router.navigate(['/home']);
              },
            });
          }
          else{
            alert('Invalid code. Please try again.');
            this.loginFormVisible = true;
          }
        }
      );
    }
  }

  loginWithEmail():void{
    const login: Login = {
      email: this.loginForm.value.email || "",
      password: "invalid_password",
      token:this.token,
    };
    alert("Please check your mail to log in!");  
    this.authService.loginWithEmail(login).subscribe({
      next: () => {
        
      },
    });
  }

  

}
