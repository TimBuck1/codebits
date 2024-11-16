onToyTypeChange(toyType: string) {
  const params: SelectionParams = {}; // Add any necessary params here, if needed

  this.updatedCities$ = this.cities.getOptions(params).pipe(
    map((cities) => {
      return cities.map((city) => {
        // Remove `disabled` property for the enabled city
        if (
          (toyType === 'marshal' && city.value === 'A') ||
          (toyType === 'tom' && city.value === 'B') ||
          (toyType === 'jerry' && city.value === 'C')
        ) {
          const { disabled, ...enabledCity } = city; // Remove `disabled` property
          return enabledCity;
        } else {
          return { ...city, disabled: true }; // Add `disabled` property
        }
      });
    })
  );

  // Debug: Log the updated cities
  this.updatedCities$.subscribe((updatedCities) =>
    console.log('Updated Cities:', updatedCities)
  );
}
