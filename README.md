# resource-bundle-tools

Looks for resource bundles and generates a spreadsheet with missing translations.

## Build

    sbt assembly

## Usage

    Usage: i18n.sh [export|duplicate] <path>

    Command: export
    Export missing keys as spreadsheet.
    
    Command: duplicate [options]
    List duplicate keys.
      -v | --values
            Output values
    
      <path>
            Path to project
