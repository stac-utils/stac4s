# STAC API Layer Extension

**Extension Maturity Classification: Proposal**

This API extension defines the API resources and semantics for retrieving [STAC Layers](../../../stac-spec/extensions/layer/README.md)
information defined for available STAC Items.

## Methods

| Path                    | Content-Type Header                                                                                         | Description                                                                       |
| ----------------------- | ----------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| `GET /layers/`          | [Catalog](https://github.com/radiantearth/stac-api-spec/blob/master/stac-spec/catalog-spec/catalog-spec.md) | Catalog wiht links to layers                                                      |
| `GET /layers/{layerID}` | [`GeoJSON` (`Feature`)](https://github.com/azavea/stac4s/blob/4aba9c6691fde1d5235f165454ceaef6c6b3e165/docs/stac-spec/extensions/layer/json-schema/layer-schema.json)                                                                                       | [STAC Layer](../../../stac-spec/extensions/layer/README.md) as a GeoJSON Feature. |

## Schema

- [JSON Static Layer Schema](../../../stac-spec/extensions/layer/json-schema/layer-schema.json)

## How It Works

When this extension is implemented, the API supports `layers` endpoint that list all layers available. An example of the result output can be found [here](../../../stac-spec/extensions/layer/examples/landsat-stac-layers/layers/catalog.json).

The specific STAC Layer is available at `/layers/{layerID}`. The example output can be found [here](../../../stac-spec/extensions/layer/examples/landsat-stac-layers/layers/us.json)

## Layer ID

Layer ID is a unique identifier for a layer.

## Example

Request to `GET /layers`:

```javascript
{
  "stac_version": "1.0.0-beta.1",
  "stac_extensions": [],
  "id": "landsat-stac-layers",
  "title": "STAC for Landsat data",
  "description": "STAC for Landsat data",
  "links": [
    {
      "href": "./catalog.json",
      "rel": "self"
    },
    {
      "href": "./catalog.json",
      "rel": "root"
    },
    {
      "href": "./landsat-8-l1/catalog.json",
      "rel": "child"
    },
    {
      "href": "./layers/pa/catalog.json",
      "rel": "child"
    },
    {
      "href": "./layers/us/catalog.json",
      "rel": "child"
    }
  ]
}
```

Request to `GET /layers/some-layer-name`:

```javascript
{
    "type": "Feature",
    "id": "layer-us",
    "stac_extensions": [
        "layer"
    ],
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
    // properties can optionally contain layer temporal information
    "properties": {
        "start_datetime": "2018-05-01T00:00:00Z",
        "start_datetime": "2018-08-01T00:00:00Z"
    },
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
