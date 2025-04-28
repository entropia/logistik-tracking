import {Directive} from '@angular/core';
import {AbstractControl, NG_VALIDATORS, ValidationErrors, Validator} from '@angular/forms';
import {LocationDto} from '../../api/models/location-dto';
import {LocationTypeDto} from '../../api/models';

@Directive({
	selector: '[appValidateLocation]',
	providers: [{provide: NG_VALIDATORS, useExisting: ValidateLocationDirective, multi: true}],
})
export class ValidateLocationDirective implements Validator {
	validate(control: AbstractControl): ValidationErrors | null {
		if (!control.value) return null;
		let theLocation = control.value as LocationDto;
		switch (theLocation.locationType) {
			case LocationTypeDto.AtOperationCenter:
				return !theLocation.operationCenter ? {"ocRequired":true} : null;
			case LocationTypeDto.Logistics:
				return !theLocation.logisticsLocation ? {"locRequired":true} : null;
			case LocationTypeDto.SomewhereElse:
				return !theLocation.somewhereElse || theLocation.somewhereElse.trim().length == 0 ? {"locFreetextRequired":true} : null;
		}
	}
}
