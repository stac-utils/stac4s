# stac4s

[![CircleCI](https://circleci.com/gh/azavea/stac4s/tree/master.svg?style=svg)](https://circleci.com/gh/azavea/stac4s/tree/master) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Join the chat at https://gitter.im/azavea/stac4s](https://badges.gitter.im/azavea/stac4s.svg)](https://gitter.im/azavea/stac4s?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge), [![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

A scala project that provides types and basic functionality for working with [SpatioTemporal Asset Catalogs](https://stacspec.org). This library is the basis for projects like [Franklin](https://azavea.github.io/franklin/) and others.

### Usage

The following STAC types are covered by this library:
 - 2d- and 3d- Bounding Boxes
 - Collection
 - Asset
 - Catalog
 - Temporal and Spatial Extents
 - Item
 - License
 - Link & Link Types
 - Relations
 - Providers & Roles
 - Media Types

On its own this library does not provide much functionality; however, it can form a strong foundation for building catalogs and applications, especially when paired with libraries like [GeoTrellis Server](https://github.com/geotrellis/geotrellis-server) and [GeoTrellis](https://geotrellis.io).

### Contributing

Contributions can be made via [pull requests](https://github.com/azavea/stac4s/pulls). You will need to fork the repository to your personal account, create a branch, update your fork, then make a pull request. Additionally, if you find a bug or have an idea for a feature/extension we would appreciate it if you opened an [issue](https://github.com/azavea/stac4s/issues) so we can work on a fix.

### Deployments, Releases, and Maintenance

`master` signals the current unreleased, actively developed codebase. Each release will have an associated `git tag` is handled automatically via a CI job once a tag is pushed.

Active development and backports for a particular _minor_ version of `stac4s` will be tracked and maintained on a branch for that series (e.g. `series/0.1.x`, `series/0.2.x`, etc). Pull requests to backport a fix, feature, or other change should be made to that series' respective branch.

Care will be taken to try and maintain backwards binary compatibility for all minor and bugfix releases.
