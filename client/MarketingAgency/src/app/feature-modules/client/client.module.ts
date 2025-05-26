import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MyProfileComponent } from './my-profile/my-profile.component';
import { ReactiveFormsModule } from '@angular/forms';
import { CreateRequestComponent } from './create-request/create-request.component';
import { YourAdvertisementComponent } from './your-advertisement/your-advertisement.component';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';



@NgModule({
  declarations: [
    MyProfileComponent,
    CreateRequestComponent,
    YourAdvertisementComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
  ]
})
export class ClientModule { }
