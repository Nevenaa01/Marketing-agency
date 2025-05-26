import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './navbar/navbar.component';
import { MaterialModule } from 'src/app/infrastructure/material/material.module';
import { RouterModule } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { ConfirmLoginComponent } from './confirm-login/confirm-login.component';

import { ReactiveFormsModule } from '@angular/forms';
import { VerifiyUserPageComponent } from './verifiy-user-page/verifiy-user-page.component';
import { AdminPermissionsComponent } from './admin-permissions/admin-permissions.component';
import { NotificationComponent } from './notification/notification.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { VpnMessagePopupComponent } from './vpn-message-popup/vpn-message-popup.component';


@NgModule({
  declarations: [
    HomeComponent,
    NavbarComponent,
    AdminDashboardComponent,
    ConfirmLoginComponent,
    VerifiyUserPageComponent,
    AdminPermissionsComponent,
    NotificationComponent,
    VpnMessagePopupComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    RouterModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  exports: [
    NavbarComponent,
    HomeComponent,
    AdminDashboardComponent
  ]
})
export class LayoutModule { }
