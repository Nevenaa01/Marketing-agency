import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './infrastructure/routing/app-routing.module';
import { AppComponent } from './app.component';
import { LayoutModule } from './feature-modules/layout/layout.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './infrastructure/material/material.module';
import { AuthModule } from './infrastructure/auth/auth.module';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { JwtInterceptor } from './infrastructure/auth/jwt/jwt.interceptor';
import { ClientModule } from './feature-modules/client/client.module';
import { ToastrModule } from 'ngx-toastr';
import { EmployeeModule } from './feature-modules/employee/employee.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LayoutModule,
    ClientModule,
    EmployeeModule,
    BrowserAnimationsModule,
    MaterialModule,
    AuthModule,
    HttpClientModule,
    ToastrModule.forRoot({
      timeOut: 4000,
      extendedTimeOut:1000,
      maxOpened:3,
      positionClass:'toast-bottom-right',
      progressBar:true,
      progressAnimation:'increasing'
    }),
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
