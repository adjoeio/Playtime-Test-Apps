import { StyleSheet, Text, TouchableOpacity } from "react-native";
import { theme } from "../util/theme";
import { textStyles } from "../util/style";

type ButtonProps = {
    title: string;
    type?: 'primary' | 'link';
    onPress: () => void;
}

const styles = StyleSheet.create({
    container: {
        padding: 12,
        backgroundColor: theme.primaryColor,
        borderRadius: 12,
        flexDirection: 'row',
        justifyContent: 'center',
    },
    linkContainer: {
        backgroundColor: 'transparent',
    },
});

export default function Button({ title, onPress, type = 'primary' }: ButtonProps) {
    return (
        <TouchableOpacity 
            onPress={onPress} 
            style={[styles.container, type === 'link' && styles.linkContainer]}
        >
            <Text 
                style={[
                    textStyles.textBold, 
                    textStyles.colorWhite, 
                    type === 'link' && textStyles.colorPrimary,
                ]}
            >
                {title}
            </Text>
        </TouchableOpacity>
    )
}
