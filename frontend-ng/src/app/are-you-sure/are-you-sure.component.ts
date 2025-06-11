import {Component, Inject, TemplateRef} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {NgTemplateOutlet} from '@angular/common';
import {MatButton} from '@angular/material/button';

export interface ConfirmScreenConfig<T> {
	title: string;
	body: TemplateRef<unknown>;
	choices: Choice<T>[];
}

export interface Choice<T> {
	title: string;
	style?: string;
	token: T;
}

export function openAreYouSureOverlay<T>(diag: MatDialog, conf: ConfirmScreenConfig<T>) {
	return diag.open<AreYouSureComponent<T>, ConfirmScreenConfig<T>, T>(AreYouSureComponent, {
		data: conf
	})
}

@Component({
  selector: 'app-are-you-sure',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		NgTemplateOutlet
	],
  templateUrl: './are-you-sure.component.html',
  styleUrl: './are-you-sure.component.scss'
})
export class AreYouSureComponent<T> {
	constructor(private diag: MatDialogRef<AreYouSureComponent<T>>, @Inject(MAT_DIALOG_DATA) protected data: ConfirmScreenConfig<T>) {}

	sendAction(a: T) {
		this.diag.close(a)
	}
}
