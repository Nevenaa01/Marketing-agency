import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeeProfileComponent } from './employee-profile/employee-profile.component';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import { RequestsComponent } from './requests/requests.component';
import { CreateAdComponent } from './create-ad/create-ad.component';
import { MatDialogModule } from '@angular/material/dialog';



@NgModule({
  declarations: [
    EmployeeProfileComponent,
    RequestsComponent,
    CreateAdComponent,
  ],
  imports: [
    CommonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
  ]
})
export class EmployeeModule { }
