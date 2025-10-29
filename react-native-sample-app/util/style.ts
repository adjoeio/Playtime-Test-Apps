import { StyleSheet } from "react-native";
import { theme } from "./theme";

export const flexStyles = StyleSheet.create({
    flex1: {
        flex: 1,
    },
    justfiyCenter: {
        justifyContent: 'center',
    },
    row: {
        flexDirection: 'row',
    },
    justifyEnd: {
        justifyContent: 'flex-end',
    },
    justifyBetween: {
        justifyContent: 'space-between',
    },
});

export const spacingStyles = StyleSheet.create({
    padding: {
        padding: 16,
    },
    margin: {
        margin: 16,
    },
    marginTop: {
        marginTop: 16,
    },
    marginBottom: {
        marginBottom: 16,
    },
    marginRight: {
        marginRight: 16,
    },
});

export const textStyles = StyleSheet.create({
    textCenter: {
        textAlign: 'center',
    },
    textBold: {
        fontWeight: 'bold',
    },
    textLarge: {
        fontSize: 18,    
    },
    textSmall: {
        fontSize: 12,
    },
    colorWhite: {
        color: 'white',
    },
    colorPrimary: {
        color: theme.primaryColor,
    },
    strikeThrough: {
        color: theme.blueGrey,
        textDecorationStyle: 'solid',
        textDecorationLine: 'line-through',
    },
    greyText: {
        color: theme.blueGrey,
    },
});

export const rewardLevelStyles = StyleSheet.create({
    levelContainer: {
        borderRadius: 12,
        padding: 2,
    },
    levelInnerContainer: {
        borderRadius: 12,
        width: 130,
        backgroundColor: theme.white,
    },
});
