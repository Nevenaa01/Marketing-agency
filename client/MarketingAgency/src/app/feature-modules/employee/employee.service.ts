import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/env/environment";
import { User } from "./model/user.model";
import { UserInformation } from "../client/model/userinformation.model";
import { Ad } from "./model/ad.model";

@Injectable({
    providedIn: 'root'
})
export class EmployeeService{
    constructor(private http: HttpClient){}

    getUserById(id: number): Observable<User>{
        return this.http.get<User>(environment.apiHost + 'user/personal-info/' + id);
    }

    updateUser(user: User): Observable<User>{
        return this.http.put<User>(environment.apiHost + 'user/employee', user);
    }

    getAllAdsByEmployeeId(id: number): Observable<Ad[]>{
        return this.http.get<Ad[]>(environment.apiHost + 'ad/findAllByEmployeeId/' + id);   
    }

    getByRequestId(id: number): Observable<Ad>{
        return this.http.get<Ad>(environment.apiHost + 'ad/findByRequestId/' + id);
    }

    createAdd(ad: Ad): Observable<Ad>{
        return this.http.post<Ad>(environment.apiHost + 'ad', ad);
    }
}