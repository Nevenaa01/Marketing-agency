import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'xp-vpn-message-popup',
  templateUrl: './vpn-message-popup.component.html',
  styleUrls: ['./vpn-message-popup.component.css']
})
export class VpnMessagePopupComponent {
  public message: string = "";

  constructor(
    public dialogRef: MatDialogRef<VpnMessagePopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {message: string}) {
      this.message = data.message;
    }
}
