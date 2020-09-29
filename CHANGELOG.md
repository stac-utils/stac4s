## Unreleased

### Changed

- All implicit imports from cats are moved to specific `cats.syntax.foo` imports [#146](https://github.com/azavea/stac4s/pull/146)
- Time types are derived from `org.joda.time.Instant` instead of stock `java` time types [#152](https://github.com/azavea/stac4s/pull/152)
- Remove joda time [#153](https://github.com/azavea/stac4s/pull/153)

### Added

### Deprecated

### Removed

### Fixed

### Security

## [0.0.14](https://github.com/azavea/stac4s/tree/0.0.14)

### Changed

- Generators for bboxes now always generate valid bboxes [#135](https://github.com/azavea/stac4s/pull/135)

## [0.0.13](https://github.com/azavea/stac4s/tree/0.0.13)

### Added

- STAC 1.0.0-beta.1 support [#116](https://github.com/azavea/stac4s/pull/116)
- STAC Layer extension spec [#126](https://github.com/azavea/stac4s/pull/126)

### Changed

- Update `StacMediaType` for `geotiff` and `cog` in 1.0.0-beta.1 spec [#132](https://github.com/azavea/stac4s/pull/132)

### Deprecated

### Removed

- Remove STAC Catalof specs against manually created catalogs from tests [#126](https://github.com/azavea/stac4s/pull/126)

### Fixed

- STAC Catalogs do not require the `stac_extensions` field [#127](https://github.com/azavea/stac4s/pull/127)

### Security

## [0.0.10](https://github.com/azavea/stac4s/tree/0.0.10)

### Added

- Publication of `testing` module, which includes `scalacheck` generators for STAC base and extension types [#104](https://github.com/azavea/stac4s/pull/104)

### Changed

- Receive GPG key while publishing artifacts [#101](https://github.com/azavea/stac4s/pull/101)

## [0.0.9](https://github.com/azavea/stac4s/tree/0.0.9)

### Fixed

- Vendor enum (link types, media types, etc.) representations no longer prefix `vendor-` in serialization [#94](https://github.com/azavea/stac4s/pull/94)
- Missing `derived_from` link type was included [#94](https://github.com/azavea/stac4s/pull/94)
- Decoding collections from json does not require the `properties` field [#97](https://github.com/azavea/stac4s/pull/97)

## [0.0.8](https://github.com/azavea/stac4s/tree/0.0.8)

### Added

- Created typeclasses for linking extensions to the items they extend [#85](https://github.com/azavea/stac4s/pull/85)
- Created extension data model for EO Extension [#92](https://github.com/azavea/stac4s/pull/92)

### Changed

- Reduce boilerplate in fieldnames derivation [#90](https://github.com/azavea/stac4s/issues/90)

### Fixed

- Stopped generating `NaN`s as valid `Double` values [#91](https://github.com/azavea/stac4s/pull/91)

## [0.0.4]

### Added

- Added ability to transform `Bbox` to `Extent` [\#5](https://github.com/azavea/stac4s/pull/5)

### Changed

- Updated to GeoTrellis 3.2 [\#5](https://github.com/azavea/stac4s/pull/5)

### Removed

- Removed `core` from package naming [\#5](https://github.com/azavea/stac4s/pull/5)

## [0.0.3]

### Added

- Added ability to transform `Bbox` to `Extent` [\#5](https://github.com/azavea/stac4s/pull/5)

### Changed

- Updated to GeoTrellis 3.2 [\#5](https://github.com/azavea/stac4s/pull/5)

### Removed

- Removed `core` from package naming [\#5](https://github.com/azavea/stac4s/pull/5)
