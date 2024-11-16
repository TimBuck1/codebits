import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

interface SelectedDatasource {
  getOptions: () => Observable<{ value: string, disabled: boolean }[]>;
}

@Component({
  selector: 'app-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.css']
})
export class DropdownComponent implements OnInit {

  // Define your options source
  options: SelectedDatasource;

  // Simulate the received value that will enable/disable options
  selectedDatasource: string = 'B';  // This could come dynamically

  modifiedOptions: { value: string, disabled: boolean }[];

  constructor() {
    // Initialize the options source with a sample method
    this.options = {
      getOptions: () => {
        return of([
          { value: 'A', disabled: true },
          { value: 'B', disabled: true },
          { value: 'C', disabled: true }
        ]);
      }
    };
  }

  ngOnInit(): void {
    // Fetch and modify the options when the component initializes
    this.options.getOptions().pipe(
      map(options => {
        // Modify the options based on the selected value
        return options.map(option => {
          // Disable everything except the selectedDatasource
          option.disabled = option.value !== this.selectedDatasource;
          return option;
        });
      })
    ).subscribe(modifiedOptions => {
      this.modifiedOptions = modifiedOptions;  // Store the modified list
    });
  }

  // Method to dynamically update the selected datasource
  updateSelectedDatasource(newValue: string): void {
    this.selectedDatasource = newValue;

    // Re-modify the options whenever the selected value changes
    this.options.getOptions().pipe(
      map(options => {
        return options.map(option => {
          option.disabled = option.value !== this.selectedDatasource;
          return option;
        });
      })
    ).subscribe(modifiedOptions => {
      this.modifiedOptions = modifiedOptions;
    });
  }
}
