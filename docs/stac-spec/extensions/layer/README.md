# STAC Layer extension

- **Title: Layer**
- **Identifier: layer**
- **Field Name Prefix: layer**
- **Scope: Item**

This document explains the fields of the STAC Layer Extension to a STAC Item. [STAC Items](https://github.com/radiantearth/stac-spec/tree/master/item-spec) can only have reference to a single [STAC Collection](https://github.com/radiantearth/stac-spec/tree/master/collection-spec).

To group items into some collection without this extension is only possible by creating full items copy (with new identifiers) that are linked to the same, new collection which we can use as a new layer or group.

This extension allows to overcome this limitation and allows items to have references to multiple named catalogs which can be used to group items by the same (layer) name. Such items can belong to a single or to multiple collections.

Extensions proposes `item.properties` field definition as well as the static STAC Layers representation (as [STAC Catalogs](https://github.com/radiantearth/stac-spec/tree/master/catalog-spec)).

## Examples

- [Landsat 8 STAC Layer catalog](examples/landsat-stac-layers/catalog.json)
- [STAC Layers static representation](examples/landsat-stac-layers/layers/catalog.json)

## Schema

- [JSON Schema](json-schema/schema.json)

## Item fields

| Field Name     | Type     | Name        | Description                                                                                      |
| -------------- | ---------| ------------|------------------------------------------------------------------------------------------------- |
| layer:ids      | [string] | Layer Names |**REQUIRED** A list of catalog identifiers which are considered as layers (a list of layer names) |

### Item properties example

```javascript
{
    "id" : "LC81530252014153LGN00",
    // ...
    "properties": {
        "collection": "item-collection",
        "layer:ids" : ["layer-us", "layer-pa"],
        "datetime": "2018-07-08T15:45:34Z",
        "view:sun_azimuth": 125.31095515,
        "view:sun_elevation": 65.2014335,
        "eo:cloud_cover": 0,
        "landsat:row": "033",
        "landsat:column": "015",
        "landsat:product_id": "LC08_L1TP_015033_20180708_20180717_01_T1",
        "landsat:scene_id": "LC80150332018189LGN00",
        "landsat:processing_level": "L1TP",
        "landsat:tier": "T1",
        "proj:epsg": 32618,
        "instruments": ["OLI_TIRS"],
        "view:off_nadir": 0,
        "platform": "landsat-8",
        "gsd": 15
    }
    // ...
}
```

## The Static Layer representation

The static catalog representation is a [STAC Catalog](https://github.com/radiantearth/stac-spec/tree/master/catalog-spec) with `links` to items that belong to such `layer`. The `id` of such catalog is a `Layer Name`.

### Static Layers example

STAC Layers can be decribed as a [STAC Catalog](https://github.com/radiantearth/stac-spec/tree/master/catalog-spec). The example of such catalog defintion is located [here](examples/landsat-stac-layers/layers/catalog.json).
Each STAC Layer is described as a [GeoJSON Feature](https://geojson.org/schema/Feature.json). Geometry represents the combined footprint of all items that belong to such Layer and the bounding box is the extent of the entire layer.

```javascript
{
    "type": "Feature",
    "id": "layer-us",
    // ... 
    // bbox and geometry that cover all items that belong to the layer
    "bbox": [
        -101.40824987104652,
        37.79718802132125,
        -73.94863288954222,
        41.41061537114088
    ],
    "geometry": {
        "type": "Polygon",
        "coordinates": [
            [
                [
                    -101.40824987104652,
                    37.79718802132125
                ],
                [
                    -101.40824987104652,
                    41.41061537114088
                ],
                [
                    -73.94863288954222,
                    41.41061537114088
                ],
                [
                    -73.94863288954222,
                    37.79718802132125
                ],
                [
                    -101.40824987104652,
                    37.79718802132125
                ]
            ]
        ]
    },
    "properties": null,
    "links": [
        {
            "rel": "self",
            "href": "./self.json"
        },
        {
            "rel": "parent",
            "href": "catalog.json"
        },
        {
            "rel": "root",
            "href": "catalog.json"
        },
        {
            "href": "item-1.json",
            "rel": "item"
        },
        {
            "href": "item-2.json",
            "rel": "item"
        },
        {
            "href": "item-3.json",
            "rel": "item"
        },
        {
            "href": "item-4.json",
            "rel": "item"
        }
    ]
}
```

## Implementations

[GeoTrellis Server](https://github.com/geotrellis/geotrellis-server/) can use the [STAC API](https://github.com/radiantearth/stac-api-spec) server (it was tested with [Franklin](https://github.com/azavea/franklin)) as an input source of rasters. It uses [STAC Layer Extension](./) to retrieve items that belong to a certain OGC Layer.

[Franklin](https://github.com/azavea/franklin) implements the [Query API Extension](https://github.com/radiantearth/stac-api-spec/tree/master/extensions/query) which allows an efficient search by the `item.properties` fields.

[STAC4S](https://github.com/azavea/stac4s) simplifies [reading/writing](https://github.com/azavea/stac4s/blob/master/modules/core/src/main/scala/com/azavea/stac4s/extensions/layer/LayerItemExtension.scala) STAC Items that follow this extension.

## Extensions

This extension doesn't require any other extensions usages. However, to use it with [STAC API](https://github.com/radiantearth/stac-api-spec/)
it would be required to use its implementation with the [Query API Extension](https://github.com/radiantearth/stac-api-spec/tree/master/extensions/query) support.

Optionally [STAC API Layer extension](../../../stac-api-spec/extensions/layer/README.md) can be implement to retrieve available STAC Layer metadata,
and to use the advantage of [STAC Layers static representation](examples/landsat-stac-layers/layers/catalog.json)
