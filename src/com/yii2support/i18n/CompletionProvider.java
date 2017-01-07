package com.yii2support.i18n;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpExpression;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

/**
 * Created by NVlad on 06.01.2017.
 */
public class CompletionProvider extends com.intellij.codeInsight.completion.CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        StringLiteralExpression psiElement = (StringLiteralExpression) parameters.getPosition().getParent();

        MethodReference methodReference = (MethodReference) psiElement.getParent().getParent();
        PhpExpression classReference = methodReference.getClassReference();
        if (classReference != null && classReference.getName() != null) {
            if (methodReference.isStatic() && classReference.getName().equals("Yii")) {
                String methodName = methodReference.getName();
                if (methodName != null && methodName.equals("t") && methodReference.getParameterList() != null) {
                    PsiElement[] methodParameters = methodReference.getParameterList().getParameters();

                    int parameterIndex = -1;
                    for (int i = 0; i < methodParameters.length; i++) {
                        if (psiElement == methodParameters[i]) {
                            parameterIndex = i;
                            break;
                        }
                    }

                    switch (parameterIndex) {
                        case 0:
                            fillCategories(psiElement, result);
                            break;
                        case 1:
                            if (methodParameters[0] instanceof StringLiteralExpression) {
                                String category = ((StringLiteralExpression) methodParameters[0]).getContents();
                                fillMessageKeys(psiElement, category, result);
                            }
                            break;
                    }
                }
            }
        }
    }

    private void fillCategories(StringLiteralExpression element, CompletionResultSet result) {
        for (PsiElement category : Util.getCategories(element)) {
            result.addElement(new CategoryLookupElement(category));
        }
    }

    private void fillMessageKeys(StringLiteralExpression element, String category, CompletionResultSet result) {
        for (String messageKey : Util.getMessageKeys(element, category)) {
            result.addElement(new MessageKeyLookupElement(messageKey));
        }
    }
}
