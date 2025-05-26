import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { BehaviorSubject } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private stompClient: Stomp.Client;
  private notificationSubject: BehaviorSubject<string> = new BehaviorSubject<string>('');

  constructor(private snackBar: MatSnackBar
  ) {
  }

  initializeWebSocketConnection() {
        const socket = new SockJS('https://localhost:8443/api/ws-endpoint');
        this.stompClient = Stomp.over(socket);

        this.stompClient.connect({}, frame => {
          this.stompClient.subscribe('/topic/push-notification', notification => {
            console.log(notification)
            this.notificationSubject.next(notification.body);
            this.openSnackBar(notification.body);
          });
        }, error => {
          console.error('Error connecting to WebSocket:', error);
        });
  }
  openSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000, 
      horizontalPosition: 'end',
      verticalPosition: 'bottom', 
      
    });
  }

  getNotificationSubject(): BehaviorSubject<string> {
    return this.notificationSubject;
  }

}
