import {Component, Input} from '@angular/core';
import {LocationDto} from '../api/models/location-dto';
import {NgIf} from '@angular/common';
import {LocationTypeDto} from '../api/models';

@Component({
  selector: 'app-location',
  imports: [
    NgIf
  ],
  templateUrl: './location.component.html',
  styleUrl: './location.component.scss'
})
export class LocationComponent {
  @Input() location?: LocationDto;

  isLOC() {
    return this.location?.locationType == LocationTypeDto.Logistics;
  }

  isOC() {
    return this.location?.locationType == LocationTypeDto.AtOperationCenter;
  }


  isSomewhereElse() {
    return this.location?.locationType == LocationTypeDto.SomewhereElse;
  }
}
