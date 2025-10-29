import { View, Modal, StyleSheet, TouchableOpacity, Text } from "react-native";
import { flexStyles, spacingStyles } from "../util/style";

type DrawerProps = {
    visible: boolean;
    onRequestClose: () => void;
    children: React.ReactNode;
};

const styles = StyleSheet.create({
    modalContainer: {
        backgroundColor: 'rgba(0, 0, 0, 0.1)',
    },
    drawerContent: {
        backgroundColor: 'white',
        borderTopLeftRadius: 10,
        borderTopRightRadius: 10,
    },
});

export default function Drawer({
    visible,
    onRequestClose,
    children,
}: DrawerProps) {
    return (
        <Modal
            visible={visible}
            transparent={true}
            animationType="fade"
            onRequestClose={onRequestClose}
        >
           <View style={[flexStyles.flex1, flexStyles.justifyEnd, styles.modalContainer]}>
                <View style={[styles.drawerContent, spacingStyles.padding]}>
                    <View style={[flexStyles.row, flexStyles.justifyEnd]}>
                        <TouchableOpacity onPress={onRequestClose}>
                            <Text>X</Text>
                        </TouchableOpacity>
                    </View>
                    <View style={[spacingStyles.marginTop]}>
                        {children}
                    </View>
                </View>
           </View> 
        </Modal>
    )
}
