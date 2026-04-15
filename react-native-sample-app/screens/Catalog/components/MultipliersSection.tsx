import { StyleSheet, Text, View } from "react-native";
import { PlaytimeRewardActionMultiplier } from "react-native-adjoe-sdk/src-ts/common/PlaytimeStudio";
import { theme } from "../../../util/theme";
import RewardsContainer from "../../../components/RewardsContainer";
import { getMultiplierBackground, multiplierIsPending } from "../../../util/campaignsHelper";
import { flexStyles, rewardLevelStyles, spacingStyles, textStyles } from "../../../util/style";

type MultiplierSectionProps = {
    multipliers: PlaytimeRewardActionMultiplier[];
}

const styles = StyleSheet.create({
    multiplierInnerContainer: {
        borderRadius: 12,
        width: 175,
        backgroundColor: theme.white,
    },
    percentageContainer: {
        padding: 8,
        borderRadius: 12,
    },
});

export default function MultiplierSection({ multipliers }: MultiplierSectionProps) {
    return (
        <RewardsContainer
            total={multipliers.length}
            sectionTitle="MULTIPLIERS"
        >
            {multipliers.map((multiplier, i) => {
                const isPending = multiplierIsPending(multiplier)
                const backgroundColor = getMultiplierBackground(multiplier)

                return (
                    <View key={i} style={[flexStyles.justfiyCenter]}>
                        <View
                            style={[
                                rewardLevelStyles.levelContainer,
                                spacingStyles.marginRight,
                                { backgroundColor },
                            ]}
                        >
                            <View style={[styles.multiplierInnerContainer, spacingStyles.padding]}>
                                <View style={[flexStyles.row, flexStyles.justfiyCenter]}>
                                    <View style={[styles.percentageContainer, { backgroundColor }]}>
                                        <Text style={[textStyles.textLarge, textStyles.colorWhite]}>
                                            {multiplier.multiplierFactorPercentage}X
                                        </Text>
                                    </View>
                                </View>

                                {isPending ? (
                                    <>
                                        <Text style={[textStyles.textCenter, textStyles.greyText]}>
                                            For {multiplier.multiplierLevels} rewards
                                        </Text>
                                        <Text style={[textStyles.textCenter]}>
                                            {multiplier.eventDescription}
                                        </Text>
                                    </>
                                ) : (
                                    <Text style={[textStyles.textCenter]}>
                                        Active for {multiplier.usedLevels} time-based levels
                                    </Text>
                                )}
                            </View>
                        </View>
                    </View>
                )
            })}
        </RewardsContainer>
    )
}
