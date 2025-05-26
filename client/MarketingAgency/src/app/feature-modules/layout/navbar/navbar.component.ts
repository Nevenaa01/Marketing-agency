import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { User } from 'src/app/infrastructure/auth/model/user.model';
import { LayoutService } from '../layout.service';
import { VpnMessagePopupComponent } from '../vpn-message-popup/vpn-message-popup.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'xp-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  user: User | undefined;
  message: string;

  constructor(private authService: AuthService,
    private layoutService: LayoutService,
    public dialog: MatDialog) {}

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      this.user = user;
    });
  }

  onLogout(): void {
    this.authService.logout();
  }

  getVpnMessage(): void{
    this.layoutService.getVPNMessage().subscribe({
      next: (res => {
        this.message = res.message;
        this.dialog.open(VpnMessagePopupComponent, {
          data: {message: this.message}
        });
      }),
      error: (error: any) => console.log(error),
      complete: (): any => {}
    });
  }
}
