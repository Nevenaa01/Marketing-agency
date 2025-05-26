import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';

@Component({
  selector: 'xp-admin-permissions',
  templateUrl: './admin-permissions.component.html',
  styleUrls: ['./admin-permissions.component.css']
})
export class AdminPermissionsComponent implements OnInit{
  constructor(
    private authService: AuthService
  ){}

  
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

}
