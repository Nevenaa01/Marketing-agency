import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { ClientService } from '../client.service';
import { ActivatedRoute } from '@angular/router';
import { UserInformation } from '../model/userinformation.model';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'xp-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css']
})
export class MyProfileComponent implements OnInit {
  
  constructor(
    private authServic: AuthService,
    private service: ClientService,
    private route: ActivatedRoute,
    private toastr: ToastrService,
    public dialog: MatDialog
  ){}
  
  user : UserInformation = {
    id: 0,
    name: '',
    surname: '',
    email: '',
    phoneNumber: '',
    adress: '',
    city: '',
    country: '',
    packageType: ""
  };

  updateBtn : boolean = false;
  goldUser : boolean = false;

  private userId: Subscription;

  ngOnInit(): void {
    this.userId = this.route.params.subscribe((params) => {
      this.getUserInfo(params["id"])

    });
  }
  getUserInfo(userId: number) {
    this.service.getUserInfo(userId).subscribe({
      next: (result:UserInformation)=>{
        this.user = result
        if(this.user.packageType == "GOLDEN"){
          this.goldUser = true;
        }
        this.matchData(this.user);
      }
    })
  }

  profileForm = new FormGroup({
    name: new FormControl('',Validators.required),
    surname: new FormControl('',Validators.required),
    email: new FormControl('',[Validators.required, Validators.email]),
    phonenumber: new FormControl('',Validators.required),
    address: new FormControl('',Validators.required),
    city: new FormControl('',Validators.required),
    country: new FormControl('',Validators.required)
  });

  matchData(user: UserInformation) {
    this.profileForm.setValue({
      name: user.name,
      surname: user.surname,
      email: user.email,
      phonenumber: user.phoneNumber,
      address: user.adress,
      city: user.city,
      country: user.country
    });

    this.disableForm();
  }

  enableForm() {
    this.profileForm.enable();
  }


  disableForm() {
    this.profileForm.disable();
  }

  editButton():void{
    this.enableForm();
    this.updateBtn = true;

  }
  updateButton():void{
    if(this.profileForm.valid){
      this.user.name = this.profileForm.value.name || '';
      this.user.surname = this.profileForm.value.surname || '';
      this.user.phoneNumber = this.profileForm.value.phonenumber || '';
      this.user.email = this.profileForm.value.email || '';
      this.user.adress = this.profileForm.value.address || '';
      this.user.city = this.profileForm.value.city || '';
      this.user.country = this.profileForm.value.country || '';

      this.service.updateUserInfo(this.user).subscribe({
        next:()=>{
          this.toastr.success('Your profile is updated!','Success');
          this.updateBtn = false;
          this.disableForm();
        },error: () =>{
          this.toastr.error('There was an error while trying to update your profile','Error');
        }
      })

    }else{
      if (this.profileForm.controls['email'].errors && this.profileForm.controls['email'].errors['email']) {
        this.toastr.error('Invalid email format', 'Validation Error');
      } else {
        this.toastr.error('You must fill in all fields.', 'Validation Error');
      }
    }
  }
  deactivateUser() {
    this.authServic.logout();
    this.service.deleteAllDataByUser(this.user.id).subscribe({
      next: (result: string) => {
        this.toastr.success(result, "Success");
      },
      error: (err: any) => {
        this.toastr.error(err.error, "Error");
      }
    });
  }
       
  


}

