import { ReactNode } from "react"
import { SafeAreaView } from "react-native-safe-area-context";

type SafeAreaProps = {
    children: ReactNode;
}

export default function SafeArea({ children }: SafeAreaProps) {
    return (
        <SafeAreaView style={{ flex: 1 }}>
            {children}
        </SafeAreaView>
    )
}
