import { Component } from '@angular/core';
import { of } from 'rxjs';

@Component({
  selector: 'app-toy-group-selector',
  templateUrl: './toy-group-selector.component.html',
  styleUrls: ['./toy-group-selector.component.css']
})
export class ToyGroupSelectorComponent {
  visibleCities: { value: string; disabled: boolean }[] = []; // Visible dropdown options

  // Simulate the observable provided by getOptions()
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
    this.loadCities(); // Initialize cities
  }

  // Load cities from the observable
  loadCities() {
    this.cities.getOptions().subscribe((data) => {
      this.visibleCities = data;
    });
  }

  // Update cities dynamically based on the selected toyType
  onToyTypeChange(toyType: string) {
    this.loadCities(); // Reset all to their initial state

    // Enable the relevant option
    switch (toyType) {
      case 'marshal':
        this.enableCity('A');
        break;
      case 'tom':
        this.enableCity('B');
        break;
      case 'jerry':
        this.enableCity('C');
        break;
    }
  }

  // Helper method to enable a specific city
  enableCity(cityValue: string) {
    const city = this.visibleCities.find((c) => c.value === cityValue);
    if (city) {
      city.disabled = false;
    }
  }
}
