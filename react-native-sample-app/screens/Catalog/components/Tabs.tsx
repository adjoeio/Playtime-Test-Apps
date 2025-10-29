import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { flexStyles, spacingStyles, textStyles } from "../../../util/style";
import { theme } from "../../../util/theme";

type TabsProps = {
    activeTab: 'catalog' | 'installed';
    setActiveTab: (tab: 'catalog' | 'installed') => void;
};

const styles = StyleSheet.create({
    header: {
        borderBottomWidth: 1,
        zIndex: 1000,
    },
});

export default function Tabs({
    activeTab,
    setActiveTab,
}: TabsProps) {
    return (
        <View style={[flexStyles.row, styles.header]}>
            <TouchableOpacity
                onPress={() => setActiveTab('catalog')}
                style={[
                    flexStyles.flex1, 
                    flexStyles.justfiyCenter, 
                    flexStyles.row, 
                    spacingStyles.padding,
                ]}
            >
                <Text 
                    style={[
                        textStyles.textLarge,
                        textStyles.textBold,
                        activeTab === 'catalog' && { color: theme.primaryColor },
                    ]}
                >
                    Catalog
                </Text>
            </TouchableOpacity>

            <TouchableOpacity
                onPress={() => setActiveTab('installed')}
                style={[
                    flexStyles.flex1, 
                    flexStyles.justfiyCenter, 
                    flexStyles.row, 
                    spacingStyles.padding,
                ]}
            >
                <Text 
                    style={[
                        textStyles.textLarge,
                        textStyles.textBold,
                        activeTab === 'installed' && { color: theme.primaryColor },
                    ]}
                >
                    My Games
                </Text>
            </TouchableOpacity>
        </View>
    );
}
