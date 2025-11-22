from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch
import torch.nn.functional as F
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'  # Suppress TensorFlow logs

# Path to your trained model
MODEL_DIR = "Sylwia454/emotion"

# Load model and tokenizer (compatible with various models)
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
tokenizer = AutoTokenizer.from_pretrained(MODEL_DIR)
model = AutoModelForSequenceClassification.from_pretrained(MODEL_DIR)
model.to(device)
model.eval()


def classify_text(texts, batch_size=16, return_probs=False, threshold=None):
    """
    texts: str or list[str]
    return_probs: if True returns dicts with label and probability distribution
    threshold: optional float in (0,1); if provided and max_prob < threshold, label will be 'uncertain'
    """
    single = False
    if isinstance(texts, str):
        single = True
        texts = [texts]

    # sanitize inputs
    texts = [t if (isinstance(t, str) and t.strip() != "") else "<EMPTY>" for t in texts]

    results = []
    for i in range(0, len(texts), batch_size):
        batch = texts[i:i+batch_size]
        enc = tokenizer(batch, truncation=True, padding=True, max_length=128, return_tensors="pt")
        enc = {k: v.to(device) for k, v in enc.items()}
        with torch.no_grad():
            out = model(**enc)
            logits = out.logits
            probs = F.softmax(logits, dim=-1).cpu()
            preds = torch.argmax(probs, dim=-1).cpu().tolist()
            for p_idx, prob in zip(preds, probs.tolist()):
                max_prob = max(prob)
                if threshold is not None and max_prob < threshold:
                    label = "uncertain"
                else:
                    label = "positive" if p_idx == 1 else "negative"
                if return_probs:
                    results.append({"label": label, "probs": prob})
                else:
                    results.append(label)

    return results[0] if single else results


if __name__ == "__main__":
    import sys
    if len(sys.argv) > 1:
        # Assume first argument is text file path
        text_path = sys.argv[1]
        with open(text_path, "r", encoding="utf-8") as f:
            text = f.read().strip()
        result = classify_text(text, return_probs=True)
        if isinstance(result, dict):
            print(result["label"])
        elif isinstance(result, list):
            print(result[0]["label"])
        else:
            print(result)
    else:
        # quick sanity-check
        examples = ["This is a great product, I recommend it!", "Very bad, I do not recommend it."]
        print(classify_text(examples, return_probs=True))
