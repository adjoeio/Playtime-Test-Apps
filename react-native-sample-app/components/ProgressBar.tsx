import { StyleSheet, View } from "react-native";
import { flexStyles } from "../util/style";

type ProgressBarType = {
    percentage: number;
}

const styles = StyleSheet.create({
    container: {
        height: 12,
        borderRadius: 12,
        backgroundColor: '#EDEFF2',
    },
    mainContainer: {
        height: '100%',
        borderRadius: 12,
        backgroundColor: '#3FCB9B',
    },
});

export default function ProgressBar({ percentage }: ProgressBarType) {
    return (
        <View style={[styles.container, flexStyles.row]}>
            <View style={[styles.mainContainer, { width: `${percentage}%` }]} />
        </View>
    )
}
