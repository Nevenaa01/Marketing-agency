import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { DenialRequest } from "src/app/infrastructure/auth/model/denial-request.model";
import { Registration } from "src/app/infrastructure/auth/model/registration.model";
import { ChangePassword } from "src/app/shared/model/change-password.model";
import { UserData } from "src/app/shared/model/user-data";
import { environment } from "src/env/environment";

@Injectable({
    providedIn: 'root'
  })
  export class LayoutService {
    constructor(private http: HttpClient){}

    getAllUsers(): Observable<UserData[]> {
        return this.http.get<UserData[]>(environment.apiHost+'user/admin');
    }

    getUserById(id:number){
      return this.http.get<UserData>(environment.apiHost+'user/'+id);
    }

    updateUser(user: UserData){
      return this.http.put<UserData>(environment.apiHost+'user/', user);
    }

    changeUserPassword(user: ChangePassword){
      return this.http.post<ChangePassword>(environment.apiHost+'auth/changePassword', user);
    }

    denyUser(user: DenialRequest){
      return this.http.post<DenialRequest>(environment.apiHost+'user/deny', user);
    }

    sendActivationLink(userId: number | undefined, userEmail: string){
      return this.http.get<String>(environment.apiHost+'user/generateActivationLink/'+userId+'/'+userEmail);
    }

    deleteUser(userId: number |undefined){
      return this.http.delete<String>(environment.apiHost+'user/'+userId);
    }

    getVPNMessage(): Observable<any>{
      return this.http.get<any>(environment.apiHost + 'user/vpn');
    }
  }