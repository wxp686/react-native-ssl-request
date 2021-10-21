# react-native-ssl-request

[React Native] ssl-authentication

## 前言

本库是在[react-native-ssl-pinning](https://github.com/MaxToyberman/react-native-ssl-pinning.git)上进行改动，原库封装了请求fetch，并加上了单向的ssl绑定，因不满足本人业务需求进行了改动，目前只是单向认证，如改为双向认证需稍稍改下代码
- [x] iOS

## Getting started

`$ npm install react-native-ssl-request --save`

### Mostly automatic installation

`$ react-native link react-native-ssl-request`

## Usage
```javascript
import SslRequest from 'react-native-ssl-request';

// TODO: What to do with the module?
SslRequest.fetch;
```
