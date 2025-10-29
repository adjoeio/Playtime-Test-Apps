import { ReactNode } from "react";
import { ScrollView, Text, View } from "react-native";
import { flexStyles, spacingStyles, textStyles } from "../util/style";

type RewardsContainerProps = {
    total: number;
    sectionTitle?: string;
    children: ReactNode;
};

export default function RewardsContainer({
    total,
    children,
    sectionTitle = "REWARDS",
}: RewardsContainerProps) {
    return (
        <View style={[spacingStyles.marginBottom]}>
            <View style={[flexStyles.row, spacingStyles.marginBottom]}>
                <View style={[flexStyles.flex1]}>
                    <Text style={[textStyles.textBold]}>
                        {sectionTitle}
                    </Text>
                </View>

                <Text>
                    {total} Total
                </Text>
            </View>

            <ScrollView horizontal>
                {children}
            </ScrollView>
        </View>
    );
}
