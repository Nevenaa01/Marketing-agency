import { Component, Inject, OnInit } from '@angular/core';
import { Ad } from '../model/ad.model';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { DatePipe } from '@angular/common';
import { RequestsComponent } from '../requests/requests.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EmployeeService } from '../employee.service';
import { RequestAdvertisement } from '../../client/model/request-advertisement.model';
import { ClientService } from '../../client/client.service';

@Component({
  selector: 'xp-create-ad',
  templateUrl: './create-ad.component.html',
  styleUrls: ['./create-ad.component.css']
})
export class CreateAdComponent implements OnInit{
  ad: Ad = {
    id: 0,
    clientId: 0,
    employeeId: 0,
    slogan: '',
    description: '',
    duration: 0,
    posted: 0,
    requestId: 0,
    clientName: ''
  }
  employeeId: number
  posted: Date
  duration: Date

  constructor(private authService: AuthService,
    @Inject(MAT_DIALOG_DATA) public data: {requestId: number, clientId: number, description: string, posted: number[], expireDate: number[]},
    private dialogRef: MatDialogRef<RequestsComponent>,
    private employeeService: EmployeeService,
    private clientService: ClientService){
    this.employeeId = this.authService.user$.getValue().id;
    this.ad.employeeId = this.employeeId;
    this.ad.clientId = data.clientId;
    this.ad.description = data.description;
    this.ad.posted = new Date(data.posted[0], data.posted[1], data.posted[2], data.posted[3], data.posted[4]).getTime()
    this.ad.duration = new Date(data.expireDate[0], data.expireDate[1], data.expireDate[2], data.expireDate[3], data.expireDate[4]).getTime()
    this.ad.requestId = data.requestId;
  }

  ngOnInit(): void {
  }

  formatMillisecondsToDate(milliseconds: number): string {
    const datePipe = new DatePipe('en-US');
    const formattedDate = datePipe.transform(milliseconds, 'dd.MM.yyyy HH:mm');
    return formattedDate || '';
  }

  create(): void{
    
    if(this.ad.slogan === ''){
      alert("Slogan cant be empty");
      return;
    }
    this.employeeService.createAdd(this.ad).subscribe({
      next: (result => {}),
      error: (error: any) => console.log(error),
      complete: (): any => {
        this.dialogRef.close()
      }
    })
  }

  cancel(): void{
    this.dialogRef.close();
  }
}
