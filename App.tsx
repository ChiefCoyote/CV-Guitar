/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useEffect, useState } from 'react';
import {Camera, useCameraDevice, useCameraPermission, VisionCameraProxy, Frame, useFrameProcessor, VideoFormat, CameraProps } from 'react-native-vision-camera';
import type {PropsWithChildren} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
} from 'react-native';

import {
  Colors,
  DebugInstructions,
  Header,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

type SectionProps = PropsWithChildren<{
  title: string;
}>;

const plugin = VisionCameraProxy.initFrameProcessorPlugin('hand_landmarks', {});

export function hand_landmarks(frame: Frame) {
    'worklet';
    if(plugin == null){
        throw new Error('Failed to load Frame Processor Plugin! 1.0');
    }
    return plugin.call(frame);
}

function Section({children, title}: SectionProps): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <View style={styles.sectionContainer}>
      <Text
        style={[
          styles.sectionTitle,
          {
            color: isDarkMode ? Colors.white : Colors.black,
          },
        ]}>
        {title}
      </Text>
      <Text
        style={[
          styles.sectionDescription,
          {
            color: isDarkMode ? Colors.light : Colors.dark,
          },
        ]}>
        {children}
      </Text>
    </View>
  );
}

function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  const device = useCameraDevice('front');
  const {hasPermission, requestPermission} = useCameraPermission();


  useEffect(() => {
      requestPermission();
  }, [requestPermission]);

  const frameProcessor = useFrameProcessor(frame => {
      'worklet';
      const data = hand_landmarks(frame);
      console.log(data);
      }, []);

  const [format, setFormat] = useState<VideoFormat | null>(null);
  useEffect(() =>{
      if (device) {
          const availableFormats = device.formats;
          const selectedFormat = availableFormats[0];
          setFormat(selectedFormat);
          }
      }, [device]);

      if (device == null || format == null) return <Text>Loading camera...</Text>;



  if (!hasPermission) {
      return <Text>No permission</Text>;
  }

  if (device == null) {
        return <Text>No device</Text>;
  }

  return (
    <Camera style={StyleSheet.absoluteFill} device={device} isActive={true} frameProcessor={frameProcessor} fps={30} pixelFormat="rgb" format={format}/>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
