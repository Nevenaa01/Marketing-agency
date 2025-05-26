import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ValidatorFn, AbstractControl } from '@angular/forms';
import { Registration } from '../model/registration.model';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { OnInit } from '@angular/core';

@Component({
  selector: 'xp-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})



export class RegistrationComponent {

  clickedRegister: boolean;
  role: string = "";

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}


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
    package: new FormControl('', [Validators.required])
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
 
  onRadioChange(event: any) {
    const selectedValue = event.target.value;

    let surname = document.querySelector('#surname');

    surname!.textContent = (selectedValue !== 'Individual') ? 'PIB' : 'Surname';
    this.role = (selectedValue !== 'Individual') ? "mod" : "";
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
      packageType: this.registrationForm.value.package || "",
      role: [this.role] || null
    };

    if (this.registrationForm.valid) {
      this.authService.register(registration).subscribe({
        next: () => {
          this.router.navigate(['home']);
        },
      });
    }
    this.clickedRegister = true;
  }
}
