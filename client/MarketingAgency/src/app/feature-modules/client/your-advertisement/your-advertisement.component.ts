import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { ClientService } from '../client.service';
import { Ad } from '../../employee/model/ad.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'xp-your-advertisement',
  templateUrl: './your-advertisement.component.html',
  styleUrls: ['./your-advertisement.component.css']
})
export class YourAdvertisementComponent implements OnInit{
  constructor(
    private authService: AuthService,
    private service: ClientService,){}

  yourAdvertisement : Ad[];
    
  ngOnInit(): void {
    this.service.getAllAdvertisementByClientId(this.authService.user$.value.id).subscribe({
      next:(result : Ad[])=>{
        this.yourAdvertisement = result;
      }
    })
  }

  formatMillisecondsToDate(milliseconds: number): string {
    const datePipe = new DatePipe('en-US');
    const formattedDate = datePipe.transform(milliseconds, 'dd.MM.yyyy HH:mm');
    return formattedDate || '';
  }


}
