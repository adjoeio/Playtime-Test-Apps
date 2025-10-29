import { StatusBar, useColorScheme, View } from 'react-native';
import CatalogScreen from './screens/Catalog';
import { flexStyles } from './util/style';
import { SafeAreaProvider } from 'react-native-safe-area-context';

export default function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaProvider>
      <View style={[flexStyles.flex1]}>
        <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
        <CatalogScreen />
      </View>
    </SafeAreaProvider>
  );
}
