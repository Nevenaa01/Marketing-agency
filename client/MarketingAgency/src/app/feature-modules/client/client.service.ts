import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserInformation } from './model/userinformation.model';
import { environment } from 'src/env/environment';
import { Observable } from 'rxjs';
import { RequestAdvertisement } from './model/request-advertisement.model';
import { Ad } from '../employee/model/ad.model';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  constructor(private http: HttpClient) { }

  getUserInfo(userId: number): Observable<UserInformation> {
    return this.http.get<UserInformation>(environment.apiHost + 'user/personal-info/' + userId);
  }

  updateUserInfo(userInfo: UserInformation): Observable<UserInformation>{
    return this.http.put<UserInformation>(environment.apiHost + 'user/update-user', userInfo);
  }

  createRequestAdvertisement(requestAdvertisement: RequestAdvertisement): Observable<RequestAdvertisement>{
    return this.http.post<RequestAdvertisement>(environment.apiHost + 'requestadvertisement', requestAdvertisement);
  }

  getRequestsAdvertisements(userId: number): Observable<RequestAdvertisement[]>{
    return this.http.get<RequestAdvertisement[]>(environment.apiHost + 'requestadvertisement/' + userId);
  }

  getAllRequests(): Observable<RequestAdvertisement[]>{
    return this.http.get<RequestAdvertisement[]>(environment.apiHost + 'requestadvertisement')
  }

  deleteRequestAdvertisement(advertisement: RequestAdvertisement) {
    return this.http.delete(environment.apiHost + 'requestadvertisement/' + advertisement.id, { responseType: 'text' });
  }

  getAllAdvertisementByClientId(clientId:number): Observable<Ad[]>{
    return this.http.get<Ad[]>(environment.apiHost + 'ad/findByClientId/' + clientId);
  }

  deleteAllDataByUser(userId: number){
    return this.http.delete(environment.apiHost + 'user/delete_all_my_data/' + userId, { responseType: 'text' });
  }

}
