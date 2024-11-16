import { Component } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-toy-group-selector',
  templateUrl: './toy-group-selector.component.html',
  styleUrls: ['./toy-group-selector.component.css']
})
export class ToyGroupSelectorComponent {
  updatedCities$: Observable<{ value: string; disabled: boolean }[]>;

  // Original cities object
  cities = {
    getOptions: () => {
      return of([
        { value: 'A', disabled: true },
        { value: 'B', disabled: true },
        { value: 'C', disabled: true }
      ]);
    }
  };

  constructor() {
    // Initialize updatedCities$ with the original options
    this.updatedCities$ = this.cities.getOptions();
  }

  // Update the options dynamically based on the selected toyType
  onToyTypeChange(toyType: string) {
    this.updatedCities$ = this.cities.getOptions().pipe(
      map((cities) =>
        cities.map((city) => ({
          ...city,
          disabled: !this.shouldEnableCity(city.value, toyType) // Enable only the required city
        }))
      )
    );
  }

  // Helper function to determine if a city should be enabled
  shouldEnableCity(cityValue: string, toyType: string): boolean {
    switch (toyType) {
      case 'marshal':
        return cityValue === 'A';
      case 'tom':
        return cityValue === 'B';
      case 'jerry':
        return cityValue === 'C';
      default:
        return false;
    }
  }
}
