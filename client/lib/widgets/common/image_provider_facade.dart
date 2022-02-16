import 'package:flutter/material.dart';

/// Not all [ImageProvider] implementations work in all situations. For instance, [AssetImage] throws errors which cannot be caught in test situations
/// because the respective assets cannot be accessed. This facade around such accessors provides a level of abstraction to prevent such code from
/// breaking during tests by enabling stubbing and mocking.
class ImageProviderFacade {
  const ImageProviderFacade();

  /// Returns an [AssetImage] based on the specified [assetName].
  ImageProvider<Object>? fromAsset(final String assetName) {
    return AssetImage(assetName);
  }
}
