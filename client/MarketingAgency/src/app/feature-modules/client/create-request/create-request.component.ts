import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { RequestAdvertisement } from '../model/request-advertisement.model';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { ClientService } from '../client.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'xp-create-request',
  templateUrl: './create-request.component.html',
  styleUrls: ['./create-request.component.css']
})
export class CreateRequestComponent implements OnInit{

  constructor(
    private authService: AuthService,
    private service: ClientService,
    private toastr: ToastrService,
  ){

  }

  requestForm = new FormGroup({
    deadline: new FormControl('',Validators.required),
    activefrom: new FormControl('',Validators.required),
    activeto: new FormControl('',Validators.required),
    description: new FormControl('',Validators.required)
  });

  requestAdvertisement : RequestAdvertisement = {
    id: 0,
    userId: 0,
    deadlineDate: new Date(),
    activeFrom: new Date(),
    activeTo: new Date(),
    description: ''
  }

  yourRequestsAdvertisement : RequestAdvertisement[];
  today: Date;


  ngOnInit(): void {
    this.today = new Date();
    this.service.getRequestsAdvertisements(this.authService.user$.value.id).subscribe({
      next:(results:RequestAdvertisement[])=>{
        this.yourRequestsAdvertisement = results;
      }
    })
  }

  createRequest():void{
    if(this.requestForm.valid){
      this.requestAdvertisement.userId = this.authService.user$.value.id;
      this.requestAdvertisement.deadlineDate = new Date(this.requestForm.value.deadline!);
      this.requestAdvertisement.activeFrom = new Date(this.requestForm.value.activefrom!);
      this.requestAdvertisement.activeTo = new Date(this.requestForm.value.activeto!);
      this.requestAdvertisement.description = this.requestForm.value.description || '';
      
      this.service.createRequestAdvertisement(this.requestAdvertisement).subscribe({
        next:(result:RequestAdvertisement)=>{
          this.clearRequestForm();
          this.toastr.success('Your request is created!','Success');
          this.ngOnInit();
        },error: () =>{
          this.toastr.error('There was an error while trying to create a request.','Error');
        }
      });

    }else{
      this.toastr.error('You must fill in all fields.','Validation Error');
    }

  }

  getDateTime(date: Date | number[]): string {
    let dateTime: Date;
  
    if (date instanceof Date) {
      dateTime = date;
    } else if (Array.isArray(date) && date.length >= 5) {
      const [year, month, day, hour, minute] = date;
      dateTime = new Date(year, month - 1, day, hour, minute);
    } else {
      return 'Nevažeći format datuma';
    }
    const formattedDate = `${dateTime.getDate()}.${dateTime.getMonth() + 1}.${dateTime.getFullYear()} ${dateTime.getHours()}:${dateTime.getMinutes()}`;
  
    return formattedDate;
  }

  clearRequestForm() : void {
    this.requestForm.setValue({
      deadline: "",
      activefrom: "",
      activeto: "",
      description: ""
    });
  }

  deleteRequest(advertisement:RequestAdvertisement):void{
    this.service.deleteRequestAdvertisement(advertisement).subscribe({
      next:(result:string)=>{
        this.toastr.success(result,"Success")
        this.ngOnInit();

      },error: (err: any) => { 
        this.toastr.error(err.error, "Error"); 
      }
    })
  }

}

