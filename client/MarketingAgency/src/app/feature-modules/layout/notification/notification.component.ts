import { Component } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { WebSocketService } from '../web-socket.service';


@Component({
  selector: 'xp-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent {
  notification$: BehaviorSubject<string>;

  constructor(private webSocketService: WebSocketService) { }

  ngOnInit() {
    
  }
}
