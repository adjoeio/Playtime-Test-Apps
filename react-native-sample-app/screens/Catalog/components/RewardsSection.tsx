import { Text, View } from "react-native"
import RewardsContainer from "../../../components/RewardsContainer"
import { flexStyles, rewardLevelStyles, spacingStyles, textStyles } from "../../../util/style"
import { getRewardColor, RewardAction, rewardIsRepeatable } from "../../../util/campaignsHelper"

type RewardsSectionProps = {
    levels: RewardAction[]
    isInstalled: boolean
    isExpired: boolean
}

export default function RewardsSection({
    levels,
    isExpired,
    isInstalled,
}: RewardsSectionProps) {
    return (
        <RewardsContainer total={levels.length}>
            {levels.map((level, i) => {
                const isRepeatable = rewardIsRepeatable(level.reward)

                return (
                    <View key={level.key} style={[flexStyles.justfiyCenter]}>
                        <View
                            style={[
                                rewardLevelStyles.levelContainer, 
                                spacingStyles.marginRight,
                                { 
                                    backgroundColor: getRewardColor(
                                        isInstalled, 
                                        isExpired, 
                                        level.reward, 
                                        levels[i - 1]?.reward
                                    ) 
                                },
                            ]}
                        >
                            {/* Repeatable Header */}
                            {isRepeatable && (
                                <Text style={[textStyles.colorWhite, textStyles.textCenter]}>
                                    Repeatable
                                </Text>
                            )}

                            <View style={[rewardLevelStyles.levelInnerContainer, spacingStyles.padding]}>
                                <Text style={[textStyles.textBold, textStyles.textCenter]}>
                                    {level.value}
                                </Text>

                                <Text style={[textStyles.textCenter]}>
                                    {level.label}
                                </Text>
                            </View>

                            {/* Repeatable Footer */}
                            {isRepeatable && (
                                <Text style={[textStyles.colorWhite, textStyles.textCenter]}>
                                    {level.reward?.rewardsCount} times
                                </Text>
                            )}
                        </View>
                    </View>
                )
            })}
        </RewardsContainer>
    )
}
