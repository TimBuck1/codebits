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

  originalOptions: { value: string, disabled: boolean }[] = [];
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
    // Fetch and store the original options when the component initializes
    this.options.getOptions().subscribe(options => {
      this.originalOptions = options;  // Store original options
      this.modifyOptions(options);    // Initially modify options
    });
  }

  // Modify the options to enable/disable them based on the selected value
  modifyOptions(options: { value: string, disabled: boolean }[]): void {
    this.modifiedOptions = options.map(option => {
      // Disable everything except the selectedDatasource
      option.disabled = option.value !== this.selectedDatasource;
      return option;
    });
  }

  // Method to dynamically update the selected datasource
  updateSelectedDatasource(newValue: string): void {
    this.selectedDatasource = newValue;

    // Use the original options and apply modifications based on the new selected value
    this.modifyOptions(this.originalOptions);
  }
}
