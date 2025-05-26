import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CreateAdComponent } from '../create-ad/create-ad.component';
import { ClientService } from '../../client/client.service';
import { RequestAdvertisement } from '../../client/model/request-advertisement.model';
import { EmployeeService } from '../employee.service';


@Component({
  selector: 'xp-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit {

  requests: RequestAdvertisement[]
  now: Date = new Date()

  constructor(public dialog: MatDialog,
    private clientService: ClientService,
    private employeeService: EmployeeService){}

  ngOnInit(): void {
    this.clientService.getAllRequests().subscribe({
      next: (result => {
        this.requests = result
        console.log(result)
      }),
      error: (error: any) => console.log(error),
      complete: (): any => {
        this.requests.forEach(request =>{
          this.employeeService.getByRequestId(request.id).subscribe({
            next: (result => request.adCreated = result === null ? false : true),
            error: (error: any) => console.log(error),
            complete: (): any => {console.log(this.requests)}
          })
        })
      }
    })
  }

  createAd(request: RequestAdvertisement): void{
    
    const dialogRef = this.dialog.open(CreateAdComponent,{
      width: '50vw',
      height: '90vh',
      data: {requestId: request.id, clientId: request.userId, description: request.description, posted: request.activeFrom, expireDate: request.activeTo}
    })

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  getDateTime(date: Date | number[]): string {
    let dateTime: Date;
  
    if (date instanceof Date) {
      dateTime = date;
    } else if (Array.isArray(date) && date.length >= 5) {
      const [year, month, day, hour, minute] = date;
      dateTime = new Date(year, month - 1, day, hour, minute);
    } else {
      return 'Invalid date format';
    }
    const formattedDate = `${dateTime.getDate()}.${dateTime.getMonth() + 1}.${dateTime.getFullYear()} ${dateTime.getHours()}:${dateTime.getMinutes()}`;
  
    return formattedDate;
  }
}
