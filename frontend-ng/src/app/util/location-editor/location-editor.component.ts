import {Component, forwardRef} from '@angular/core';
import {LocationDto} from '../../api/models/location-dto';
import {LocationTypeDto} from '../../api/models/location-type-dto';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR, NgForm} from '@angular/forms';
import {LogisticsLocationDto, OperationCenterDto} from '../../api/models';

@Component({
  selector: 'app-location-editor',
	imports: [
		MatFormField,
		MatSelect,
		FormsModule,
		MatOption,
		MatInput,
		MatLabel
	],
  templateUrl: './location-editor.component.html',
  styleUrl: './location-editor.component.scss',
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			useExisting: forwardRef(() => LocationEditorComponent),
			multi: true,
		},
	],
})
export class LocationEditorComponent implements ControlValueAccessor {

	private _onChange: any;
	private _onTouch: any;
	private _isDisabled: boolean = false;


	writeValue(obj: any): void {
        this.location = {...(obj as LocationDto)};
    }
    registerOnChange(fn: any): void {
		this._onChange = fn;
    }
    registerOnTouched(fn: any): void {
		this._onTouch = fn;
    }
    setDisabledState?(isDisabled: boolean): void {
        this._isDisabled = isDisabled;
    }

	update() {
		console.log("updated")
		this._onChange(this.location)
		this._onTouch(true)
	}

	location: LocationDto = {locationType: LocationTypeDto.Logistics};

	protected readonly LocationTypeDto = LocationTypeDto;
	protected readonly Object = Object;
	protected readonly LogisticsLocationDto = LogisticsLocationDto;
	protected readonly OperationCenterDto = OperationCenterDto;

	whenthe(theForm: NgForm) {
		console.log(theForm)
	}
}
