import { Component, OnInit } from '@angular/core';
import { EPackage, ERole, EUserStatus, UserData } from 'src/app/shared/model/user-data';
import { LayoutService } from '../layout.service';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { Subscription } from 'rxjs';
import { TokenStorage } from 'src/app/infrastructure/auth/jwt/token.service';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Registration } from 'src/app/infrastructure/auth/model/registration.model';
import { ChangePassword } from 'src/app/shared/model/change-password.model';
import { DenialRequest } from 'src/app/infrastructure/auth/model/denial-request.model';

@Component({
  selector: 'xp-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit{
  moderators:  UserData[] = [];
  clients: UserData[] = [];
  loggedUser: UserData;
  clickedRegister: boolean;
  showPopup: boolean = false;
  showChangePassword: boolean|undefined= false;

  registrationForm = new FormGroup({
    name: new FormControl('', [Validators.required]),
    surname: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    phoneNumber: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    city: new FormControl('', [Validators.required]),
    country: new FormControl('', [Validators.required]),
    adress: new FormControl('', [Validators.required]),
    confirmPassword: new FormControl('', [Validators.required]),
  },{ validators: [this.passwordValidator, this.passwordCharactersValidator] });

  changePasswordForm = new FormGroup({
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    confirmPassword: new FormControl('', [Validators.required]),
  },{ validators: [this.passwordValidator, this.passwordCharactersValidator] });

  passwordValidator(control: AbstractControl): { [key: string]: any } | null {
    if(!control.value) return null;
    
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    
    return password && confirmPassword && password.value === confirmPassword.value ?  null : { 'passwordMismatch': true };
  }

  passwordCharactersValidator(control: AbstractControl): { [key: string]: any } | null {
    if (!control.value) return null;
  
    const password = control.get('password');
    
    if (password && password.value) {
      const regexUpperCase = /[A-Z]/;
      const regexLowerCase = /[a-z]/;
      const regexNumber = /[0-9]/;
      
      const hasUpperCase = regexUpperCase.test(password.value);
      const hasLowerCase = regexLowerCase.test(password.value);
      const hasNumber = regexNumber.test(password.value);
      
      if (hasUpperCase && hasLowerCase && hasNumber) {
        return null;
      } else {
        return { 'passwordCriteria': true };
      }
    } else {
      return { 'passwordMismatch': true };
    }
  }

  constructor(private layoutService: LayoutService, private authService: AuthService){}
  ngOnInit(): void {
    this.getLoggedUser();
    this.getClients(); 
  }

  getColorBasedOnPackageType(packageType: string): string {
    switch (packageType) {
      case 'BASIC':
        return 'cornflowerblue';
      case 'STANDARD':
        return 'crimson';
      case 'GOLDEN':
        return 'goldenrod';
      default:
        return '';
    }
  }

  register(): void {
    const registration: Registration = {
      name: this.registrationForm.value.name || "",
      surname: this.registrationForm.value.surname || "",
      email: this.registrationForm.value.email || "",
      phoneNumber: this.registrationForm.value.phoneNumber || "",
      password: this.registrationForm.value.password || "",
      city: this.registrationForm.value.city || "",
      adress: this.registrationForm.value.adress || "",
      country: this.registrationForm.value.country || "",
      packageType: 'BASIC',
      role: ["admin"]
    };

    if (this.registrationForm.valid) {

      let btn = document.querySelector('#form-btn');
      if(btn?.textContent === "Add Admin"){
        this.registerNewAdmin(registration);
      }
      else if(btn?.textContent === "Update"){

        const { role, ...userWithoutRole } = registration;

        const userData: UserData = {
          ...userWithoutRole,
          id: this.loggedUser.id,
          roles: this.loggedUser.roles,
          packageType: this.loggedUser.packageType,
          isVerified: true, 
          status: this.loggedUser.status,
        };
        this.updateAdmin(userData);
        console.table(userData);
      }
    }
    this.clickedRegister = true;
    this.showPopup = false;
    this.clickedRegister = false;
    this.registrationForm.reset();
  }

  addNewAdmin(type: string) {
    let btn = document.querySelector('#form-btn') as HTMLButtonElement;
    let header = document.querySelector('#form-header') as HTMLElement;
    let email = document.querySelectorAll('.email');
  
    if (type === "update") {
      btn!.textContent = "Update";
      header!.textContent = "Update Credentials";
      email.forEach((element: Element) => {
        (element as HTMLElement).style.display = "none";
      });

      this.populateFormWithLoggedUser(type);

    } else if (type === "add") {
      btn!.textContent = "Add Admin";
      header!.textContent = "Add new admin";

      email.forEach((element: Element) => {
        (element as HTMLElement).style.display = "block";
      });

      this.populateFormWithLoggedUser(type);
    }
  
    this.showPopup = !this.showPopup;
  }

  getClients(){
    this.moderators =[];
    this.clients = [];
    this.layoutService.getAllUsers().subscribe({
      next: (result)=>{
        result.forEach(user => {
          if(user.roles?.at(0)?.name.toString() === "ROLE_MODERATOR"){
            this.moderators.push(user);
          }
          else if(user.roles?.at(0)?.name.toString() === "ROLE_USER"){
            this.clients.push(user)
          }
        })
      },
      complete:()=>{
      }
    })
  }

  getLoggedUser(){
    this.authService.user$.subscribe(user => {
      let u = user;
      this.layoutService.getUserById(u.id).subscribe({

        next: (res) =>{
          this.loggedUser = res;
          this.showChangePassword = this.loggedUser.firstTimeLogin;
          console.table(this.loggedUser);
        }

      })
    });
  }

  registerNewAdmin(registration: Registration){
    this.authService.register(registration).subscribe({
      next: () => {

        Object.keys(this.registrationForm.controls).forEach(controlName => {
          this.registrationForm.get(controlName)?.setValue('');
        });

      },
    });
  }

  updateAdmin(registration:UserData){
    this.layoutService.updateUser(registration).subscribe({
      next: (_) =>{
        this.getLoggedUser();
      }
    });
  }

  populateFormWithLoggedUser(action: String): void {

    if(action!=='update')
      this.registrationForm.reset();
    else
      this.registrationForm.patchValue({
      email: this.loggedUser.email,
      adress: this.loggedUser.adress,
      city: this.loggedUser.city,
      confirmPassword: this.loggedUser.password,
      password: this.loggedUser.password,
      country: this.loggedUser.country,
      phoneNumber: this.loggedUser.phoneNumber,
      name: this.loggedUser.name,
      surname: this.loggedUser.surname
    });
  }

  denyUser(className: string, user: UserData){
    let {email, id, ...rest} = user;
    let inputElement = document.querySelector(className) as HTMLInputElement;
    let inputValue = inputElement.value;


    let request: DenialRequest = {
      userId: id,
      email: email,
      report: inputValue
    };
    this.layoutService.denyUser(request).subscribe({
      next: ()=>{
        this.getClients();
      }
    })
    
  }

  changePassword(){
    if(this.changePasswordForm.valid){
      let changePasswordRequest: ChangePassword = {
        newPassword: this.changePasswordForm.value.password?.toString(),
        userId: this.loggedUser.id
      };
      this.layoutService.changeUserPassword(changePasswordRequest).subscribe({

        next: (_)=>{
          this.getLoggedUser();
        }

      });
      this.showChangePassword = false;
    }
  }

  approveUser(user:UserData){
    this.layoutService.sendActivationLink(user.id, user.email).subscribe();
  }


  unblockUser(user:UserData){
    this.layoutService.deleteUser(user.id).subscribe({
      next: (_) =>{
        this.getClients();
      }
    })
  }
}
