onToyTypeChange(toyType: string) {
  this.updatedCities$ = this.cities.getOptions().pipe(
    map((cities) => {
      return cities.map((city) => {
        if (
          (toyType === 'marshal' && city.value === 'A') ||
          (toyType === 'tom' && city.value === 'B') ||
          (toyType === 'jerry' && city.value === 'C')
        ) {
          const { disabled, ...enabledCity } = city; // Remove 'disabled'
          return enabledCity;
        } else {
          return { ...city, disabled: true }; // Add 'disabled'
        }
      });
    }),
    map((updatedCities) => [...updatedCities]) // Create a new array reference
  );
}
