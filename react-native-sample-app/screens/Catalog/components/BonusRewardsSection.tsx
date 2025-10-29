import { Text, View } from "react-native"
import { humanizeMinutes } from "../../../util/date"
import RewardsContainer from "../../../components/RewardsContainer"
import { flexStyles, rewardLevelStyles, spacingStyles, textStyles } from "../../../util/style"
import { getBonusActionBackground, RewardAction, rewardIsTimeBoosted } from "../../../util/campaignsHelper"

type RewardsSectionProps = {
    levels: RewardAction[]
}

export default function BonusRewardsSection({ levels }: RewardsSectionProps) {
    return (
        <RewardsContainer total={levels.length} sectionTitle="BONUS REWARDS">
            {levels.map((level) => {
                const isTimeBoosted = level.reward ? rewardIsTimeBoosted(level.reward) : false

                return (
                    <View key={level.key} style={[flexStyles.justfiyCenter]}>
                        <View
                            style={[
                                rewardLevelStyles.levelContainer, 
                                spacingStyles.marginRight,
                                { 
                                    backgroundColor: getBonusActionBackground(level) 
                                },
                            ]}
                        >
                            {/* Time-Boosted header */}
                            {isTimeBoosted && (
                                <Text style={[textStyles.colorWhite, textStyles.textCenter]}>
                                    {humanizeMinutes(level.reward?.timedCoinsDuration || 0)}
                                </Text>
                            )}

                            {/* Cashback header */}
                            {level.cashback ? (
                                <Text style={[textStyles.colorWhite, textStyles.textCenter]}>
                                    Repeatable
                                </Text>
                            ) : null}
                            
                            <View style={[rewardLevelStyles.levelInnerContainer, spacingStyles.padding]}>
                                {isTimeBoosted && (
                                    <Text 
                                        style={[
                                            textStyles.textBold, 
                                            textStyles.strikeThrough, 
                                            textStyles.textSmall, 
                                            textStyles.textCenter,
                                        ]}
                                    >
                                        {level.value}
                                    </Text>
                                )}

                                <Text style={[textStyles.textBold, textStyles.textCenter]}>
                                    {isTimeBoosted ? level.reward?.timedCoins : level.value}
                                </Text>

                                <Text style={[textStyles.textCenter]}>
                                    {level.label}
                                </Text>
                            </View>

                            {/* Cashback footer */}
                            {level.cashback ? (
                                <Text style={[textStyles.colorWhite, textStyles.textCenter]}>
                                    {`${level.cashback.exchangeRate} for $1`}
                                </Text>
                            ) : null}
                        </View>
                    </View>
                )
            })}
        </RewardsContainer>
    )
}
