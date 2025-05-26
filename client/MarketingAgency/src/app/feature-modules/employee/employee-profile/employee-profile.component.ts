import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { User } from '../model/user.model';
import { EmployeeService } from '../employee.service';
import { Ad } from '../model/ad.model';
import { DatePipe } from '@angular/common';
import { LayoutService } from '../../layout/layout.service';
import { ClientService } from '../../client/client.service';
import { ToastrService } from 'ngx-toastr';
import { UserInformation } from '../../client/model/userinformation.model';

@Component({
  selector: 'xp-employee-profile',
  templateUrl: './employee-profile.component.html',
  styleUrls: ['./employee-profile.component.css']
})
export class EmployeeProfileComponent implements OnInit{
  employee: User = {
    id: 0,
    email: '',
    name: '',
    surname: '',
    phoneNumber: '',
    adress: '',
    city: '',
    country: '',
    packageType: ''
  }
  ads: Ad[] 
  employeeId: number
  edit: boolean = false
  goldUser : boolean = false;
  
  constructor(private authService: AuthService,
    private employeeService: EmployeeService,
    private layoutService: LayoutService,
    private clientService: ClientService,
    private toastr: ToastrService){
    this.employeeId = this.authService.user$.getValue().id;
  }

  ngOnInit(): void {
    this.employeeService.getUserById(this.employeeId).subscribe({
      next: (result:User) => {
        this.employee = result;
        if(this.employee.packageType == "GOLDEN"){
          this.goldUser = true;
        }
      },
      error: (error: any) => console.log(error),
      complete: (): any => {
        this.getAllAds()
      }
    })
  }

  getAllAds(): void{
    this.employeeService.getAllAdsByEmployeeId(this.employeeId).subscribe({
      next: (result => this.ads = result),
      error: (error: any) => console.log(error),
      complete: (): any => {
        this.ads.forEach(ad =>{
          this.layoutService.getUserById(ad.clientId).subscribe({
            next: (result => ad.clientName = result.name + " " + result.surname),
            error: (error: any) => console.log(error),
            complete: (): any => {}
          })
        })
      }
    })
  }

  enableEditing(): void{
    this.edit = !this.edit;
  }

  update(): void{
    if(this.employee.name === '' || 
      this.employee.surname === '' ||
      this.employee.phoneNumber === '' ||
      this.employee.city === '' ||
      this.employee.country === '' ||
      this.employee.adress === ''){
        alert("Fields cannot be empty")
        return;
      }
      
    this.employeeService.updateUser(this.employee).subscribe({
      next: (result => {this.employee = result}),
      error: (error: any) => console.log(error),
      complete: (): any => {this.edit = !this.edit;}
    })
  }

  formatMillisecondsToDate(milliseconds: number): string {
    const datePipe = new DatePipe('en-US');
    const formattedDate = datePipe.transform(milliseconds, 'dd.MM.yyyy HH:mm');
    return formattedDate || '';
  }

  deactivateUser() {
    this.authService.logout();
    this.clientService.deleteAllDataByUser(this.employee.id!).subscribe({
      next: (result: string) => {
        this.toastr.success(result, "Success");
      },
      error: (err: any) => {
        this.toastr.error(err.error, "Error");
      }
    });
  }
       
}
