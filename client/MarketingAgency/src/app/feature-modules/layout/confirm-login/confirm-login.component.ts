import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { WebSocketService } from '../web-socket.service';

@Component({
  selector: 'xp-confirm-login',
  templateUrl: './confirm-login.component.html',
  styleUrls: ['./confirm-login.component.css']
})
export class ConfirmLoginComponent {
  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  flag:boolean=false;

  ngOnInit(): void {
    const token : string = this.route.snapshot.paramMap.get('token')||"";
    this.authService.confirmLoginWithEmail(token).subscribe({
      next: () => {
        this.flag=true;
        
      },
    });
  }
  
}
