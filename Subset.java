onToyTypeChange(toyType: string) {
  // Update the observable by applying logic to modify the emitted values
  this.updatedCities$ = this.cities.getOptions().pipe(
    map((cities) => {
      console.log('Original Cities:', cities); // Log the original array for debugging
      return cities.map((city) => {
        // Enable or disable based on the toyType
        if (toyType === 'marshal' && city.value === 'A') {
          console.log(`Enabling City: ${city.value}`); // Log when enabling
          return { ...city, disabled: false };
        } else if (toyType === 'tom' && city.value === 'B') {
          console.log(`Enabling City: ${city.value}`); // Log when enabling
          return { ...city, disabled: false };
        } else if (toyType === 'jerry' && city.value === 'C') {
          console.log(`Enabling City: ${city.value}`); // Log when enabling
          return { ...city, disabled: false };
        } else {
          return { ...city, disabled: true }; // Keep others disabled
        }
      });
    })
  );

  // For debugging, subscribe temporarily to print the transformed data
  this.updatedCities$.subscribe((updatedCities) =>
    console.log('Updated Cities:', updatedCities)
  );
}
