import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {NgOptimizedImage} from '@angular/common';
import {MatButton} from '@angular/material/button';

export interface Choice {
	title: string;
	style?: string;
}

@Component({
  selector: 'app-are-you-sure',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		NgOptimizedImage,
		MatDialogActions,
		MatButton
	],
  templateUrl: './are-you-sure.component.html',
  styleUrl: './are-you-sure.component.scss'
})
export class AreYouSureComponent {
	constructor(private diag: MatDialogRef<AreYouSureComponent>, @Inject(MAT_DIALOG_DATA) protected data: Choice[]) {}

	sendAction(a: Choice) {
		this.diag.close(a)
	}
}
