# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]
### Fixed
- JVM StacCollections also have a type [#299](https://github.com/azavea/stac4s/pull/299)

## [0.2.1] - 2021-04-20
### Fixed
- Bounded generators to prevent downstream test speed and memory issues [#290](https://github.com/azavea/stac4s/pull/290)

## [0.2.0] - 2021-04-19
### Added
- Tested bbox union invariants [#266](https://github.com/azavea/stac4s/pull/266)
- Modeled periodic extent and made intervals extensible [#276](https://github.com/azavea/stac4s/pull/276)
- Updated models for compatibility with spec version 1.0.0-rc2 (breaking) [#283](https://github.com/azavea/stac4s/pull/283)

## [0.1.1] - 2021-03-12
### Fixed
- Told circle only to publish tags that start with `v` [#190](https://github.com/azavea/stac4s/pull/190)
- Review client specs and make them more deterministic [#212](https://github.com/azavea/stac4s/pull/212)

### Added
- Added `StacLayer` and `StacLayerProperties` types [#252](https://github.com/azavea/stac4s/pull/252)
- Bboxes can be unioned and form a semigroup [#259](https://github.com/azavea/stac4s/pull/259)

## [0.0.21] - 2021-01-08
### Fixed
- Fix Client signatures [#210](https://github.com/azavea/stac4s/pull/210)

## [0.0.20] - 2021-01-04
### Added
- Сlient module [#140](https://github.com/azavea/stac4s/pull/140)

## [0.0.19] - 2020-12-11
### Fixed
- Repaired build.sbt configuration to get sonatype publication to cooperate [#186](https://github.com/azavea/stac4s/pull/186)

## [0.0.18] - 2020-11-23
### Added
- Added cross-project configuration for Scala.js modules [#157](https://github.com/azavea/stac4s/pull/157)

## [0.0.17] - 2020-11-11
### Changed
- SPDX license ids are captured by a specific enum rather than a refinement with validation [#172](https://github.com/azavea/stac4s/pull/172)

## [0.0.16] - 2020-09-30
### Changed
- Remove joda time [#153](https://github.com/azavea/stac4s/pull/153)

## [0.0.15] - 2020-09-29
### Changed
- All implicit imports from cats are moved to specific `cats.syntax.foo` imports [#146](https://github.com/azavea/stac4s/pull/146)
- Time types are derived from `org.joda.time.Instant` instead of stock `java` time types [#152](https://github.com/azavea/stac4s/pull/152)

## [0.0.14] - 2020-08-06
### Changed
- Generators for bboxes now always generate valid bboxes [#135](https://github.com/azavea/stac4s/pull/135)

## [0.0.13] - 2020-07-31
### Added
- STAC 1.0.0-beta.1 support [#116](https://github.com/azavea/stac4s/pull/116)
- STAC Layer extension spec [#126](https://github.com/azavea/stac4s/pull/126)

### Changed
- Update `StacMediaType` for `geotiff` and `cog` in 1.0.0-beta.1 spec [#132](https://github.com/azavea/stac4s/pull/132)

### Removed
- Remove STAC Catalof specs against manually created catalogs from tests [#126](https://github.com/azavea/stac4s/pull/126)

### Fixed
- STAC Catalogs do not require the `stac_extensions` field [#127](https://github.com/azavea/stac4s/pull/127)

## [0.0.10] - 2020-06-17
### Added
- Publication of `testing` module, which includes `scalacheck` generators for STAC base and extension types [#104](https://github.com/azavea/stac4s/pull/104)

### Changed
- Receive GPG key while publishing artifacts [#101](https://github.com/azavea/stac4s/pull/101)

## [0.0.9] - 2020-06-02
### Fixed
- Vendor enum (link types, media types, etc.) representations no longer prefix `vendor-` in serialization [#94](https://github.com/azavea/stac4s/pull/94)
- Missing `derived_from` link type was included [#94](https://github.com/azavea/stac4s/pull/94)
- Decoding collections from json does not require the `properties` field [#97](https://github.com/azavea/stac4s/pull/97)

## [0.0.8] - 2020-05-27
### Added
- Created typeclasses for linking extensions to the items they extend [#85](https://github.com/azavea/stac4s/pull/85)
- Created extension data model for EO Extension [#92](https://github.com/azavea/stac4s/pull/92)

### Changed
- Reduce boilerplate in fieldnames derivation [#90](https://github.com/azavea/stac4s/issues/90)

### Fixed
- Stopped generating `NaN`s as valid `Double` values [#91](https://github.com/azavea/stac4s/pull/91)

## [0.0.4] - 2020-03-25
### Added
- Added ability to transform `Bbox` to `Extent` [#5](https://github.com/azavea/stac4s/pull/5)

### Changed
- Updated to GeoTrellis 3.2 [#5](https://github.com/azavea/stac4s/pull/5)

### Removed
- Removed `core` from package naming [#5](https://github.com/azavea/stac4s/pull/5)

## [0.0.3] - 2019-12-20
### Added
- Added ability to transform `Bbox` to `Extent` [#5](https://github.com/azavea/stac4s/pull/5)

### Changed
- Updated to GeoTrellis 3.2 [#5](https://github.com/azavea/stac4s/pull/5)

### Removed
- Removed `core` from package naming [#5](https://github.com/azavea/stac4s/pull/5)

[Unreleased]: https://github.com/azavea/stac4s/compare/v0.2.1...HEAD
[0.2.1]: https://github.com/azavea/stac4s/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/azavea/stac4s/compare/v0.1.1...v0.2.0
[0.1.1]: https://github.com/azavea/stac4s/compare/v0.0.21...v0.1.1
[0.0.21]: https://github.com/azavea/stac4s/compare/v0.0.20...v0.0.21
[0.0.20]: https://github.com/azavea/stac4s/compare/v0.0.19...v0.0.20
[0.0.19]: https://github.com/azavea/stac4s/compare/v0.0.18...v0.0.19
[0.0.18]: https://github.com/azavea/stac4s/compare/v0.0.17...v0.0.18
[0.0.17]: https://github.com/azavea/stac4s/compare/v0.0.16...v0.0.17
[0.0.16]: https://github.com/azavea/stac4s/tree/0.0.16
[0.0.15]: https://github.com/azavea/stac4s/tree/0.0.15
[0.0.14]: https://github.com/azavea/stac4s/tree/0.0.14
[0.0.13]: https://github.com/azavea/stac4s/tree/0.0.13
[0.0.10]: https://github.com/azavea/stac4s/tree/0.0.10
[0.0.9]: https://github.com/azavea/stac4s/tree/0.0.9
[0.0.8]: https://github.com/azavea/stac4s/tree/0.0.8
[0.0.4]: https://github.com/azavea/stac4s/tree/0.0.4
[0.0.3]: https://github.com/azavea/stac4s/tree/0.0.3
