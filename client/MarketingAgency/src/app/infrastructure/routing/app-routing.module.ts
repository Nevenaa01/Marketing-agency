import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from 'src/app/feature-modules/layout/home/home.component';
import { LoginComponent } from '../auth/login/login.component';
import { RegistrationComponent } from '../auth/registration/registration.component';
import { MyProfileComponent } from 'src/app/feature-modules/client/my-profile/my-profile.component';
import { CreateRequestComponent } from 'src/app/feature-modules/client/create-request/create-request.component';
import { ConfirmLoginComponent } from 'src/app/feature-modules/layout/confirm-login/confirm-login.component';
import { AuthGuard } from '../auth/auth.guard';
import { VerifiyUserPageComponent } from 'src/app/feature-modules/layout/verifiy-user-page/verifiy-user-page.component';
import { YourAdvertisementComponent } from 'src/app/feature-modules/client/your-advertisement/your-advertisement.component';
import { EmployeeProfileComponent } from 'src/app/feature-modules/employee/employee-profile/employee-profile.component';
import { RequestsComponent } from 'src/app/feature-modules/employee/requests/requests.component';
import { AdminPermissionsComponent } from 'src/app/feature-modules/layout/admin-permissions/admin-permissions.component';
import { NotificationComponent } from 'src/app/feature-modules/layout/notification/notification.component';



const routes: Routes = [
  {path: 'home', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegistrationComponent},
  {path: 'user-profile/:id', component: MyProfileComponent, canActivate:[AuthGuard]},
  {path: 'create-request', component: CreateRequestComponent, canActivate:[AuthGuard]},
  {path: 'confirm_login/:token', component: ConfirmLoginComponent},
  {path: 'verify', component: VerifiyUserPageComponent,  canActivate:[AuthGuard]},
  {path: 'your-advertisement', component: YourAdvertisementComponent,  canActivate:[AuthGuard]},
  {path: 'requests', component: RequestsComponent, canActivate:[AuthGuard]},
  {path: 'employee-profile/:id', component: EmployeeProfileComponent, canActivate:[AuthGuard]},
  {path: 'notification', component: NotificationComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
